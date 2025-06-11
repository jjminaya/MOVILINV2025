const mysql = require('mysql2/promise');
const pool = mysql.createPool({
    host: process.env.DB_HOST,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD,
    database: process.env.DB_NAME,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

class CrudRepository{
    constructor(model){
        this.model = model;
        this.tableName = model.tableName;
        this.pk = model.pk;
        this.pool = pool;
    }

    async findAll(){
        const [rows] = await pool.query(`SELECT * FROM ${this.tableName} WHERE estado = 1`);
        return rows;
    }

    async findById(id){
        const [rows] = await pool.query(`SELECT * FROM ${this.tableName} where ${this.pk} = ?`,[id]);
        return rows[0];
    }

    async create(data){
        const [result] = await pool.query(`INSERT INTO ${this.tableName} SET ?`, data);
        return {[this.pk] : result.insertId,...data};
    }

    async update(id, data){
        await pool.query(`UPDATE ${this.tableName} SET ? WHERE ${this.pk}=?`, [data,id]);
        return this.findById(id);
    }

    async delete(id){
        const [result] = await pool.query(`UPDATE ${this.tableName} SET estado = 0 WHERE ${this.pk} =?`, [id]);
        return result.affectedRows>0;
    }
}module.exports = CrudRepository;