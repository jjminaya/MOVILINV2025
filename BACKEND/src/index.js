require('dotenv').config();
const express = require('express');

const elementoController = require('./controllers/elementoController');
const inventarioController = require('./controllers/inventarioController');
const usuarioController = require('./controllers/usuarioController');

const app = express();
app.use(express.json());




// Rutas usuario
app.get('/usuarios', usuarioController.getAll);
app.get('/usuarios/:id', usuarioController.getById);
app.post('/usuarios', usuarioController.create);
app.put('/usuarios/:id', usuarioController.update);
app.delete('/usuarios/:id', usuarioController.delete);




app.use('/api/elementos', elementoController);
app.use('/api/inventarios', inventarioController);

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Servidor corriendo en http://localhost:${PORT}`);
});
