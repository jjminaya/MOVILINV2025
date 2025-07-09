require('dotenv').config();
const express = require('express');

const elementoController = require('./controllers/elementoController');
const inventarioController = require('./controllers/inventarioController');
const colaboradorController = require('./controllers/colaboradorController');

const app = express();
app.use(express.json());

app.use('/api/elementos', elementoController);
app.use('/api/inventarios', inventarioController);
app.use('/api/colaboradores', colaboradorController);

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Servidor corriendo en http://localhost:${PORT}`);
  console.log('HOST de MySQL:', process.env.DB_HOST);
});
