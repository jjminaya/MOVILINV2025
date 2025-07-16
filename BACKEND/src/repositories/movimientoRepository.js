const CrudRepository = require("../lib/crudRepository");
const Movimiento = require('../models/movimientoModel');

class MovimientoRepository extends CrudRepository{
    constructor() {
        super(Movimiento)
    }

    async create(data) {
        const query = `
            INSERT INTO ${this.tableName} (
                idUsuario, nombreUsuario, idInventario, descripcionInventario,
                idElemento, descripcionElemento, tipoObjeto, accion, descripcion
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
        `;

        const values = [
            data.idUsuario,
            data.nombreUsuario,
            data.idInventario,
            data.descripcionInventario,
            data.idElemento,
            data.descripcionElemento,
            data.tipoObjeto,
            data.accion,
            data.descripcion
        ];

        const [result] = await this.pool.query(query, values);
        return { idMovimiento: result.insertId, ...data };
    }

    async findAll() {
        const [result] = await this.pool.query(`SELECT * FROM ${this.tableName} ORDER BY fecha DESC`);
        return result;
    }
}

module.exports = new MovimientoRepository();
