import React, { useState } from "react";

const App = () => {
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [resultMessage, setResultMessage] = useState(null);

  const handleFileChange = (e) => {
    setSelectedFiles(Array.from(e.target.files));
  };

  const handleUpload = async () => {
    try {
      const formData = new FormData();
      selectedFiles.forEach((file) => {
        formData.append("files", file);
      });

      const response = await fetch("http://localhost:8080/api/upload-images", {
        method: "POST",
        body: formData,
      });

      const result = await response.json();
      setResultMessage({ type: "success", message: result });
    } catch (error) {
      console.error("Error uploading images:", error.message);
      setResultMessage({ type: "error", message: error.message });
    }
    setTimeout(() => setResultMessage(null), 5000);
  };

  return (
    <div className="app">
      {resultMessage && (
        <div>
          {resultMessage.message.status === 200
            ? `Success: ${resultMessage.message.message}`
            : `Error: ${resultMessage.message.message}`}
        </div>
      )}
      <h1>Image Uploader</h1>
      <input
        type="file"
        accept="image/*"
        multiple
        onChange={handleFileChange}
      />
      <button onClick={handleUpload} disabled={selectedFiles.length === 0}>
        Upload Images
      </button>
    </div>
  );
};

export default App;
