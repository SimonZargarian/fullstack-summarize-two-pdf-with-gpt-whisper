import React from 'react';
import { CssBaseline, Container } from '@mui/material';
import FileUpload from './components/FileUpload';

function App() {
  return (
    <Container component="main" maxWidth="lg">
      <CssBaseline />
      <FileUpload/>
    </Container>
  );
}

export default App;
