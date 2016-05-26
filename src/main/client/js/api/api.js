const apiBaseUrl = '/api';
const serviceCommonConfig = {
	save: {
		method: 'PUT'
	},
	query: {
		method: 'GET',
		params: {},
		isArray: false,
		cache: false
	},
	filter: {
		method: 'POST',
		isArray: false,
		cache: false
	}
};
