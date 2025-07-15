require('dotenv').config();
const express = require('express');
const cors = require('cors');

const elementoController = require('./controllers/elementoController');
const inventarioController = require('./controllers/inventarioController');
const colaboradorController = require('./controllers/colaboradorController');
const usuarioController = require('./controllers/usuarioController'); // ✅ controlador de usuario (login y CRUD)

const app = express();

// 🟢 Middleware necesario
app.use(cors()); // para que funcione con tu app frontend Angular o Android
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// 🟢 Rutas API
app.use('/api/elementos', elementoController);
app.use('/api/inventarios', inventarioController);
app.use('/api/colaboradores', colaboradorController);

// 🟢 Rutas usuario (CRUD)
app.get('/usuarios', usuarioController.getAll);
app.get('/usuarios/:id', usuarioController.getById);
app.post('/usuarios', usuarioController.create);
app.put('/usuarios/:id', usuarioController.update);
app.delete('/usuarios/:id', usuarioController.delete);

// 🟢 Ruta login
app.post('/api/login', usuarioController.login);

// 🟢 Puerto de escucha
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`✅ Servidor corriendo en http://localhost:${PORT}`);
  console.log(`✅ Conectado a base de datos en ${process.env.DB_HOST}`);
});
