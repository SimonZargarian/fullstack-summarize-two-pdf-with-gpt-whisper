import React, { useState } from 'react';
import { Button, Typography, Container, Box, Grid, Paper } from '@mui/material';
import axios from 'axios';

const FileUpload = () => {
  const [selectedFiles, setSelectedFiles] = useState([null, null]);
  const [extractedTexts, setExtractedTexts] = useState(['', '']);
  const [mp3Url, setMp3Url] = useState('http://localhost:8080/summarized_speech.mp3'); // Define the mp3Url state here
  const [isUploading, setIsUploading] = useState(false);

  const handleFileChange = (index) => (event) => {
    const newFiles = [...selectedFiles];
    newFiles[index] = event.target.files[0];
    setSelectedFiles(newFiles);
  };

  const handleUpload = async () => {
    if (!selectedFiles[0] || !selectedFiles[1]) {
      alert('Please select both files before uploading.');
      return;
    }
  
    const formData = new FormData();
    formData.append('file1', selectedFiles[0]);
    formData.append('file2', selectedFiles[1]);
  
    try {
      setIsUploading(true);
      const response = await axios.post('http://localhost:8080/api/pdf/extract-texts', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
       // Assuming the backend correctly returns an object with texts and the MP3 URL
      if (response.data.texts) setExtractedTexts(response.data.texts);
      // Assuming the backend correctly returns an object with texts and the MP3 URL
      const newUrl = `http://localhost:8080/summarized_speech.mp3?${new Date().getTime()}`; // Append timestamp
    setMp3Url(newUrl); // Update the state with the new URL
    } catch (error) {
      console.error('Error uploading files:', error);
      alert('Failed to upload files.');
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <Container maxWidth="lg">
      <Typography variant="h5" sx={{ mt: 2, mb: 2, marginTop: 4, marginBottom: 4 }}>
        Upload PDFs to Hear Summary
      </Typography>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        {[0, 1].map((index) => (
          <Box key={index} sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <input
              accept="application/pdf"
              type="file"
              onChange={handleFileChange(index)}
              disabled={isUploading}
              id={`file-input-${index}`}
              style={{ display: 'none' }}
            />
            <label htmlFor={`file-input-${index}`}>
              <Button variant="contained" component="span" disabled={isUploading}>
                Select File {index + 1}
              </Button>
            </label>
            {selectedFiles[index] && (
              <Paper elevation={1} sx={{ p: 1, pl: 2, pr: 2 }}>
                {selectedFiles[index].name}
              </Paper>
            )}
          </Box>
        ))}
        <Button
          variant="contained"
          onClick={handleUpload}
          disabled={isUploading || !selectedFiles[0] || !selectedFiles[1]}
          sx={{ mt: 2 }}
        >
          {isUploading ? 'Uploading...' : 'Upload and Extract Text'}
        </Button>
      </Box>
      <Grid container spacing={2} sx={{ mt: 2 }}>
        {extractedTexts.map((text, index) => (
          <Grid item xs={6} key={index}>
            <Paper elevation={3} sx={{ p: 2, minHeight: 200 }}>
              <Typography>
                Extracted Text from File {index + 1}
              </Typography>
              <Typography variant="body2" sx={{ mt: 1 }}>
                {text}
              </Typography>
            </Paper>
          </Grid>
        ))}
      </Grid>
      {mp3Url && (
  <Box sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
    <audio controls src={mp3Url}>
      Your browser does not support the audio element.
    </audio>
  </Box>
)}
    </Container>
  );
};

export default FileUpload;
