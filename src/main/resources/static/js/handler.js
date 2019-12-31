var $percent = $('.percent');
var $bar = $('.bar');
var $duration = $('.duration');
var $progress = $('.progress');

function resetProgress() {
	$percent.text("0")
    $bar.css('width', '0%')
}

function onFileProgess(file){
        var prgress = Math.ceil(file.progress()* 100);
        $percent.text(`${prgress}%`)
        $bar.css('width', prgress+'%')
        console.log('fileProgress', prgress);
};
    
function onFileSuccess(file, message){
    console.log('file success')
    if(uploadTimer) {
        clearInterval(uploadTimer);
        $duration.text(`Time: ${timer / 60} min`);
        timer = 0;
    }
}
    
function onFileError(file, message) {
	console.log('file error',message)
}
    
function onFileAdded(file, event) {
	console.log('file added',file);
    $progress.show();
    
    uploadTimer = setInterval(() => {
        timer += 1;
        $duration.text(`Time: ${timer} sec`)
    },1000)
    r.upload()
}