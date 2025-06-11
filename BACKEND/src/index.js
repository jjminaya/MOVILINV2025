require('dotenv').config();
const express = require('express');

// const userController = require('./controllers/userController');

const app = express();
// app.use(express.json());

// app.use('/api/users', userController);

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Servidor corriendo en http://localhost:${PORT}`);
});
