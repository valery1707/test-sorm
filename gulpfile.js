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
		'src/main/client/images/**/*'
	],
	scriptsApp: [
		'src/main/client/js/**/*.js'
	],
	scriptsLibCommon: [
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

		, 'bower_components/angular-translate/angular-translate.js'
		, 'bower_components/angular-translate-loader-static-files/angular-translate-loader-static-files.js'

		, 'bower_components/ghiscoding.angular-validation/dist/angular-validation.min.js'

		, 'bower_components/moment/moment.js'

		, 'bower_components/jquery-datetimepicker/jquery.datetimepicker.js'

		, 'bower_components/FileSaver.js/index.js'
		, 'bower_components/Blob.js/index.js'
	],
	scriptsLibIE: [
		'bower_components/html5shiv/dist/html5shiv.js'
		, 'bower_components/respond/dest/respond.src.js'
	],
	stylesApp: [
		'src/main/client/css/**/*.scss'
		, 'src/main/client/css/**/*.css'
	],
	stylesLibCommon: [
		'node_modules/bootstrap/dist/css/bootstrap.css'

		, 'node_modules/angular-ui-grid/ui-grid.css'

		, 'bower_components/github-fork-ribbon-css/gh-fork-ribbon.css'

		, 'bower_components/jquery-datetimepicker/jquery.datetimepicker.css'
	],
	stylesLibIE: [
		'bower_components/github-fork-ribbon-css/gh-fork-ribbon.ie.css'
	],
	stylesLibCommonAssets: [
		'node_modules/angular-ui-grid/ui-grid.woff',
		'node_modules/angular-ui-grid/ui-grid.ttf'
	]
};

gulp.task('images', function () {
	gulp.src(paths.images)
			.pipe(cache(imagemin({optimizationLevel: 3, progressive: true, interlaced: true})))
			.pipe(gulp.dest('src/main/webapp/images/'));
});

gulp.task('stylesApp', function () {
	gulp.src(paths.stylesApp)
			.pipe(plumber({
				errorHandler: function (error) {
					console.log(error.message);
					this.emit('end');
				}
			}))
			.pipe(sass())
			.pipe(postcss([autoprefixer({browsers: ['last 2 versions']})]))
			.pipe(gulp.dest('src/main/webapp/css/'))
			.pipe(rename({suffix: '.min'}))
			.pipe(cleanCss())
			.pipe(gulp.dest('src/main/webapp/css/'));
});
gulp.task('stylesLibCommon', function () {
	gulp.src(paths.stylesLibCommon)
			.pipe(plumber({
				errorHandler: function (error) {
					console.log(error.message);
					this.emit('end');
				}
			}))
			.pipe(gulp.dest('src/main/webapp/css/common/'))
			.pipe(concat('lib.common.css'))
			.pipe(gulp.dest('src/main/webapp/css/'))
			.pipe(rename({suffix: '.min'}))
			.pipe(cleanCss())
			.pipe(gulp.dest('src/main/webapp/css/'));
	gulp.src(paths.stylesLibCommonAssets)
			.pipe(gulp.dest('src/main/webapp/css/common/'))
			.pipe(gulp.dest('src/main/webapp/css/'));
});
gulp.task('stylesLibIE', function () {
	gulp.src(paths.stylesLibIE)
			.pipe(plumber({
				errorHandler: function (error) {
					console.log(error.message);
					this.emit('end');
				}
			}))
			.pipe(gulp.dest('src/main/webapp/css/ie/'))
			.pipe(concat('lib.ie.css'))
			.pipe(gulp.dest('src/main/webapp/css/'))
			.pipe(rename({suffix: '.min'}))
			.pipe(cleanCss())
			.pipe(gulp.dest('src/main/webapp/css/'));
});

gulp.task('scriptsApp', function () {
	return gulp.src(paths.scriptsApp)
			.pipe(plumber({
				errorHandler: function (error) {
					console.log(error.message);
					this.emit('end');
				}
			}))
			.pipe(concat('app.js'))
			.pipe(gulp.dest('src/main/webapp/js/'))
			.pipe(rename({suffix: '.min'}))
			.pipe(uglify())
			.pipe(gulp.dest('src/main/webapp/js/'));
});
gulp.task('scriptsLibCommon', function () {
	return gulp.src(paths.scriptsLibCommon)
			.pipe(plumber({
				errorHandler: function (error) {
					console.log(error.message);
					this.emit('end');
				}
			}))
			.pipe(gulp.dest('src/main/webapp/js/common/'))
			.pipe(concat('lib.common.js'))
			.pipe(gulp.dest('src/main/webapp/js/'))
			.pipe(rename({suffix: '.min'}))
			.pipe(uglify())
			.pipe(gulp.dest('src/main/webapp/js/'));
});
gulp.task('scriptsLibIE', function () {
	return gulp.src(paths.scriptsLibIE)
			.pipe(plumber({
				errorHandler: function (error) {
					console.log(error.message);
					this.emit('end');
				}
			}))
			.pipe(gulp.dest('src/main/webapp/js/ie/'))
			.pipe(concat('lib.ie.js'))
			.pipe(gulp.dest('src/main/webapp/js/'))
			.pipe(rename({suffix: '.min'}))
			.pipe(uglify())
			.pipe(gulp.dest('src/main/webapp/js/'));
});

gulp.task('default', function () {
	gulp.watch(paths.stylesApp, ['stylesApp']);
	gulp.watch(paths.scriptsApp, ['scriptsApp']);
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
