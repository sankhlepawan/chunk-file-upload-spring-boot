var BROWS_BUTTON = document.getElementById('browseButton');
var uploadTimer;
var timer = 0;

var config = {
	API_URL:  "http://localhost:3000/uploader/v1/app/upload",
	CHUNK_SIZE: 1*1024*1024,  // 2 mb
	NO_OF_PARELLEL_UPLOADS: 6,
	TEST_CHUNK: true
}