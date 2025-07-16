// 📁 src/index.js
require('dotenv').config();
const express = require('express');
const cors = require('cors');

// Controladores
const elementoController = require('./controllers/elementoController');
const inventarioController = require('./controllers/inventarioController');
const colaboradorController = require('./controllers/colaboradorController');
const usuarioController = require('./controllers/usuarioController'); // 👈 controlador directo

const app = express();

// 🟢 Middlewares necesarios
app.use(cors()); // Permite peticiones desde Angular, Android, web externa, etc.
app.use(express.json()); // Soporta JSON
app.use(express.urlencoded({ extended: true })); // Soporta formularios

// 🟢 Rutas API sin router externo

// Usuarios (CRUD)
app.get('/api/usuarios', usuarioController.getAll);
app.get('/api/usuarios/:id', usuarioController.getById);
app.post('/api/usuarios', usuarioController.create);
app.put('/api/usuarios/:id', usuarioController.update);
app.delete('/api/usuarios/:id', usuarioController.delete);

// Login
app.post('/api/usuarios/login', usuarioController.login);

// Otros recursos
app.use('/api/elementos', elementoController);
app.use('/api/inventarios', inventarioController);
app.use('/api/colaboradores', colaboradorController);

// 🟢 Servidor accesible desde otras máquinas/redes
const PORT = process.env.PORT || 3000;
const HOST = '0.0.0.0'; // Necesario para exponer en red local o IP pública

app.listen(PORT, HOST, () => {
  console.log(`✅ Servidor corriendo en http://${HOST}:${PORT}`);
  console.log(`✅ Conectado a base de datos en ${process.env.DB_HOST}`);
});
