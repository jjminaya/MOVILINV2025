const usuarioRepository = require('../repositories/usuarioRepository');

class UsuarioService {
  async getAll() {
    return await usuarioRepository.findAll();
  }

  async getById(id) {
    return await usuarioRepository.findById(id);
  }

  async create(data) {
    return await usuarioRepository.create(data);
  }

  async update(id, data) {
    return await usuarioRepository.update(id, data);
  }

  async delete(id) {
    return await usuarioRepository.delete(id);
  }
}

module.exports = new UsuarioService();
