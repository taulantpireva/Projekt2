function checkFiles(files) {
    console.log(files);

    if (files.length != 1) {
        alert("Bitte genau eine Datei hochladen.")
        return;
    }

    const fileSize = files[0].size / 1024 / 1024; // in MiB
    if (fileSize > 10) {
        alert("Datei zu gross (max. 10Mb)");
        return;
    }

    answerPart2.style.visibility = "visible";
    const file = files[0];

    // Preview
    if (file) {
        preview.src = URL.createObjectURL(files[0]);
       previewText.textContent = "Analyzed Image";
    } else {
        preview.src = "default.jpg";
        previewText.textContent = "No picture uploaded";
    }

    // Upload
    const formData = new FormData();
    for (const name in files) {
        formData.append("image", files[name]);
    }

    fetch('/analyze', {
        method: 'POST',
        headers: {},
        body: formData
    }).then(response => response.json())  // Parse JSON response
      .then(data => {
          console.log(data);
          const highestProbability = data.reduce((max, item) => item.probability > max.probability ? item : max);
          const formattedResult = {
              className: highestProbability.className,
              percentage: (highestProbability.probability * 100).toLocaleString(undefined, { minimumFractionDigits: 6, maximumFractionDigits: 6 }) + "%"
          };

          // Display the highest probability result
          answer.innerHTML = `Answer: <strong>${formattedResult.className}</strong>`;
      })
      .catch(error => console.log(error));

}