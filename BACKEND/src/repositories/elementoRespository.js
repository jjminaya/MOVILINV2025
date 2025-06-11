const CrudRepository  = require('../lib/crudRepository');
const Elemento = require('../models/elementoModel');

class elementoRepository extends CrudRepository{
    constructor(){
        super(Elemento);
    }
}
module.exports = new elementoRepository();