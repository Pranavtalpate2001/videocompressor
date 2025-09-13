function uploadVideo() {
    const fileInput = document.getElementById('videoFile');
    if (!fileInput.files.length) { alert('Select a video'); return; }
    const file = fileInput.files[0];
    const formData = new FormData();
    formData.append('file', file);

    const progressBar = document.getElementById('progressBar');
    const downloadLink = document.getElementById('downloadLink');

    fetch('/compress-video', { method: 'POST', body: formData })
    .then(response => {
        if (!response.ok) throw new Error('Compression failed');
        return response.blob();
    })
    .then(blob => {
        const url = URL.createObjectURL(blob);
        downloadLink.href = url;
        downloadLink.download = 'compressed_' + file.name;
        downloadLink.style.display = 'block';
        downloadLink.innerText = 'Download Compressed Video';
        progressBar.style.width = '100%';
        progressBar.innerText = '100%';
    })
    .catch(err => alert(err.message));
}
