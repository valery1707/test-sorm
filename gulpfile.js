var gulp = require('gulp');
var plumber = require('gulp-plumber');
var rename = require('gulp-rename');
var concat = require('gulp-concat');

//JavaScript
var uglify = require('gulp-uglify');

//CSS
var cleanCss = require('gulp-clean-css');
var sass = require('gulp-sass');

var postcss = require('gulp-postcss');
var autoprefixer = require('autoprefixer');

//Images
var imagemin = require('gulp-imagemin');
var cache = require('gulp-cache');

var paths = {
	images: [
		'src/main/webapp/images/**/*'
	],
	scripts: [
		'node_modules/angular/angular.js'
		, 'src/main/webapp/scripts/**/*.js'
	],
	styles: [
		'src/main/webapp/styles/**/*.scss'
		, 'src/main/webapp/styles/**/*.css'
	]
};

gulp.task('images', function () {
	gulp.src(paths.images)
			.pipe(cache(imagemin({optimizationLevel: 3, progressive: true, interlaced: true})))
			.pipe(gulp.dest('src/main/webapp/public/'));
});

gulp.task('styles', function () {
	gulp.src(paths.styles)
			.pipe(plumber({
				errorHandler: function (error) {
					console.log(error.message);
					this.emit('end');
				}
			}))
			.pipe(sass())
			.pipe(postcss([autoprefixer({browsers: ['last 2 versions']})]))
			.pipe(gulp.dest('src/main/webapp/public/'))
			.pipe(rename({suffix: '.min'}))
			.pipe(cleanCss())
			.pipe(gulp.dest('src/main/webapp/public/'))
});

gulp.task('scripts', function () {
	return gulp.src(paths.scripts)
			.pipe(plumber({
				errorHandler: function (error) {
					console.log(error.message);
					this.emit('end');
				}
			}))
			.pipe(gulp.dest('src/main/webapp/public/debug/'))
			.pipe(concat('main.js'))
			.pipe(gulp.dest('src/main/webapp/public/'))
			.pipe(rename({suffix: '.min'}))
			.pipe(uglify())
			.pipe(gulp.dest('src/main/webapp/public/'))
});

gulp.task('default', function () {
	gulp.watch(paths.styles, ['styles']);
	gulp.watch(paths.scripts, ['scripts']);
});
