const { sequelize } = require('../config/database'); // ✅ IMPORTACIÓN NECESARIA
const { QueryTypes } = require('sequelize');
const CrudRepository = require('../lib/crudRepository');
const Usuario = require('../models/usuarioModel');

class UsuarioRepository extends CrudRepository {
  constructor() {
    super(Usuario);
  }

  async findByUsernameAndPassword(username, password) {
    const [usuario] = await sequelize.query(
      "SELECT * FROM usuario WHERE username = ? AND password = ? AND estado = 1",
      {
        replacements: [username, password],
        type: QueryTypes.SELECT
      }
    );
    return usuario;
  }
}

module.exports = new UsuarioRepository();
