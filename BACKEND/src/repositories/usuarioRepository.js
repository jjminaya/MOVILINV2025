const CrudRepository = require('../lib/crudRepository');
const Usuario = require('../models/usuarioModel');

class UsuarioRepository extends CrudRepository {
  constructor() {
    super(Usuario);
  }

  // agregar métodos personalizados si los necesitas
}

module.exports = new UsuarioRepository();
