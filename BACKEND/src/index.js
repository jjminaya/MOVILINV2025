// 📁 index.js (servidor principal Express)
require('dotenv').config();
const express = require('express');
const cors = require('cors');

const elementoController = require('./controllers/elementoController');
const inventarioController = require('./controllers/inventarioController');
const usuarioController = require('./controllers/usuarioController');
const colaboradorController = require('./controllers/colaboradorController');

const app = express();

// ✅ Middlewares necesarios para aceptar JSON y formularios
app.use(cors()); // Permitir peticiones desde otras IPs (como Android)
app.use(express.json()); // Para recibir application/json
app.use(express.urlencoded({ extended: true })); // Para x-www-form-urlencoded

// ✅ Rutas para usuarios
app.get('/usuarios', usuarioController.getAll);
app.get('/usuarios/:id', usuarioController.getById);
app.post('/usuarios', usuarioController.create);
app.put('/usuarios/:id', usuarioController.update);
app.delete('/usuarios/:id', usuarioController.delete);

// ✅ Login (usado por Android)
app.post('/api/login', usuarioController.login);

// ✅ Rutas de otros recursos
app.use('/api/elementos', elementoController);
app.use('/api/inventarios', inventarioController);
app.use('/api/colaboradores', colaboradorController);

// ✅ Escuchar en todas las interfaces (0.0.0.0)
const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`🟢 Servidor corriendo en http://0.0.0.0:${PORT}`);
});
