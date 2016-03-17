const bower = require('bower');
const _ = require('lodash');

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
		, 'node_modules/angular-route/angular-route.js'
		, 'node_modules/angular-resource/angular-resource.js'
		, 'node_modules/angular-mocks/angular-mocks.js'
		, 'node_modules/angular-touch/angular-touch.js'

		, 'bower_components/jquery/dist/jquery.js'

		, 'node_modules/bootstrap/dist/js/bootstrap.js'

		, 'node_modules/angular-ui-grid/ui-grid.js'
		, 'bower_components/csv/index.js'
		, 'bower_components/pdfmake/build/pdfmake.js'
		, 'bower_components/pdfmake/build/vfs_fonts.js'

		, 'bower_components/html5shiv/dist/html5shiv.js'
		, 'bower_components/respond/dest/respond.src.js'

		, 'bower_components/angular-translate/angular-translate.js'
		, 'bower_components/angular-translate-loader-static-files/angular-translate-loader-static-files.js'

		, 'bower_components/ghiscoding.angular-validation/dist/angular-validation.min.js'

		, 'bower_components/moment/moment.js'

		, 'bower_components/jquery-datetimepicker/jquery.datetimepicker.js'

		, 'src/main/webapp/scripts/**/*.js'
	],
	styles: [
		'node_modules/bootstrap/dist/css/bootstrap.css'

		, 'node_modules/angular-ui-grid/ui-grid.css'

		, 'bower_components/github-fork-ribbon-css/gh-fork-ribbon.css'
		, 'bower_components/github-fork-ribbon-css/gh-fork-ribbon.ie.css'

		, 'bower_components/jquery-datetimepicker/jquery.datetimepicker.css'

		, 'src/main/webapp/styles/**/*.scss'
		, 'src/main/webapp/styles/**/*.css'
	],
	stylesCopy: [
		'node_modules/angular-ui-grid/ui-grid.woff',
		'node_modules/angular-ui-grid/ui-grid.ttf'
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
			.pipe(gulp.src(paths.stylesCopy))
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

var bowerTask = function (fn, cb) {
	const depMap = require(bower.config.cwd + '/package.json').bowerDependencies;
	var depNames = _.chain(depMap).keys().value();
	const depInfo = _.chain(depNames).map(function (name) {
		const version = depMap[name];
		const package = version.startsWith('http') ? version : name + '#' + version;
		return name + '=' + package;
	}).value();
	var pkgs = [];
	bower.commands[fn](depInfo)
			.on('log', function (o) {
				if (o.id !== 'install') {
					return;
				}
				console.log('Bower install: ', o.data.endpoint.name);
				pkgs.push(o.data.endpoint.name);
			})
			.on('error', function (error) {
				console.log('Bower error:', error);
			})
			.on('end', function (installed) {
				deleteDeps(_.difference(pkgs, depNames), cb);
			});
};
gulp.task('bowerInstall', function (cb) {
	bowerTask('install', cb);
});
gulp.task('bowerUpdate', function (cb) {
	bowerTask('update', cb);
});
