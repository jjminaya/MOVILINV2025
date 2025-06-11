require('dotenv').config();
const express = require('express');

const elementoController = require('./controllers/elementoController');

const app = express();
app.use(express.json());

app.use('/api/elementos', elementoController);

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Servidor corriendo en http://localhost:${PORT}`);
});
