var r = new Resumable({
        target: config.API_URL,
        chunkSize: config.CHUNK_SIZE,
        simultaneousUploads: config.NO_OF_PARELLEL_UPLOADS,
        testChunks: config.TEST_CHUNK,
        throttleProgressCallbacks:1,
        method: "octet"
        // query:{upload_token:'my_token'}
 });
    
r.assignBrowse(BROWS_BUTTON);

r.on('fileProgress', onFileProgess);

r.on('fileSuccess', onFileSuccess);

r.on('fileError',onFileError);

r.on('fileAdded', onFileAdded);
    



