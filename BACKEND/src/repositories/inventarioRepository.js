const CrudRepository = require("../lib/crudRepository");
const Inventario = require("../models/inventarioModel");

class inventarioRepository extends CrudRepository {
  constructor() {
    super(Inventario);
  }

  async getInventariosByUserID(UserId) {
    const query = `
        SELECT i.idInventario, i.descripcionInventario, i.elementosInventario, c.rangoColaborador 
        FROM inventario i
        INNER JOIN colaborador c ON i.idInventario = c.idInventario
        WHERE c.idUsuario = ? AND c.estado = 1 AND i.estado = 1;
    `;
    const [result] = await this.pool.query(query, [UserId]);
    return result;
  }
}
module.exports = new inventarioRepository();
