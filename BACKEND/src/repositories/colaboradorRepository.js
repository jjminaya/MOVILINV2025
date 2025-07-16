const CrudRepository  = require('../lib/crudRepository');
const Colaborador = require('../models/colaboradorModel');

class colaboradorRepository extends CrudRepository{
    constructor(){
        super(Colaborador);
    }

    async verificarUsuarioValido(username) {
        const query = `
            SELECT idUsuario FROM usuario
            WHERE username = ? AND estado = 1
            LIMIT 1
        `;
        const [rows] = await this.pool.query(query, [username]);

        if (rows.length > 0) {
            return {
                valido: true,
                idUsuario: rows[0].idUsuario
            };
        } else {
            return {
                valido: false,
                idUsuario: null
            };
        }
    }

    async getAllActiveUsers() {
    const query = `
        SELECT idUsuario, username FROM usuario
        WHERE estado = 1
    `;
    const [result] = await this.pool.query(query);
    return result;
    }
}
module.exports = new colaboradorRepository();