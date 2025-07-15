const usuarioService = require('../services/usuarioService');

exports.getAll = async (req, res) => {
  const result = await usuarioService.getAll();
  res.json(result);
};

exports.getById = async (req, res) => {
  const result = await usuarioService.getById(req.params.id);
  if (!result) return res.status(404).send("Usuario no encontrado");
  res.json(result);
};

exports.create = async (req, res) => {
  const result = await usuarioService.create(req.body);
  res.status(201).json(result);
};

exports.update = async (req, res) => {
  try {
    console.log('🟡 [UPDATE] ID por params:', req.params.id);
    console.log('🟡 [UPDATE] Body recibido:', req.body);

    const result = await usuarioService.update(req.params.id, req.body);

    console.log('🟢 [UPDATE] Resultado:', result);

    res.json(result);
  } catch (error) {
    console.error('🔴 [UPDATE] Error al actualizar usuario:', error);
    res.status(500).json({ message: 'Error al actualizar usuario', error });
  }
};

exports.delete = async (req, res) => {
  const result = await usuarioService.delete(req.params.id);
  res.json({ success: result });
};

// ✅ Ruta de login corregida
exports.login = async (req, res) => {
  // Usa req.body con seguridad
  const { username, password } = req.body;

  if (!username || !password) {
    return res.status(400).json({ message: 'Faltan credenciales' });
  }

  try {
    const usuario = await usuarioService.login(username, password);

    if (usuario) {
      // Asegúrate de acceder a los datos correctamente
      const usuarioPlano = usuario.dataValues || usuario; // por si viene de Sequelize

      const { password, ...userWithoutPassword } = usuarioPlano;
      return res.json(userWithoutPassword);
    } else {
      return res.status(401).json({ message: 'Credenciales inválidas' });
    }

  } catch (error) {
    console.error('Error en login:', error);
    return res.status(500).json({ message: 'Error del servidor', error });
  }
   
};
