require('dotenv').config();
const express = require('express');

const elementoController = require('./controllers/elementoController');
const inventarioController = require('./controllers/inventarioController');

const app = express();
app.use(express.json());

app.use('/api/elementos', elementoController);
app.use('/api/inventarios', inventarioController);

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Servidor corriendo en http://localhost:${PORT}`);
});
