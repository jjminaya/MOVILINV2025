const CrudRepository = require("../lib/crudRepository");
const Inventario = require("../models/inventarioModel");

class inventarioRepository extends CrudRepository {
  constructor() {
    super(Inventario);
  }

  colaboradorTableName = "colaborador";

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

  async crearInventarioOWNR(data) {
    let connection;

    try {
      connection = await this.pool.getConnection();
      await connection.beginTransaction();

      const inventarioData = {
        descripcionInventario: data.descripcion,
        //elementosInventario: 0,
        //estado: 1,
      };

      const [inventarioResult] = await connection.query(
        `INSERT INTO ${this.tableName} SET ?`,
        inventarioData
      );
      const idInventario = inventarioResult.insertId;

      if (!idInventario) {
        throw new Error(
          "No se pudo obtener el ID del inventario recién creado."
        );
      }

      const colaboradorData = {
        idInventario: idInventario,
        idUsuario: data.userID,
        rangoColaborador: "OWNR",
        //estado: 1
      };

      await connection.query(
        `INSERT INTO ${this.colaboradorTableName} SET ?`,
        colaboradorData
      );

      await connection.commit();

      return {
        [this.pk]: idInventario,
        ...inventarioData,
        rangoColaborador: "OWNR",
      };
    } catch (error) {
      if (connection) {
        await connection.rollback();
        console.error(
          "Transacción revertida debido a un error al crear inventario y colaborador."
        );
      }
      console.error("Error en crearInventarioOWNR:", error);
      throw error;
    } finally {
      if (connection) {
        connection.release();
      }
    }
  }
}
module.exports = new inventarioRepository();
