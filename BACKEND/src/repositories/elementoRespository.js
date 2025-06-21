const CrudRepository  = require('../lib/crudRepository');
const Elemento = require('../models/elementoModel');

class elementoRepository extends CrudRepository{
    constructor(){
        super(Elemento);
    }

    async getElementosByIventarioID(InventarioId) {
    const query = `
        SELECT * FROM elementos e WHERE e.inventarioElemento = ? AND e.estado = 1
    `;
    const [result] = await this.pool.query(query, [InventarioId]);
    return result;
    }
}
module.exports = new elementoRepository();