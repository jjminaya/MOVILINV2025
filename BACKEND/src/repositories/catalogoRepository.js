const CrudRepository = require('../lib/crudRepository');

class CatalogoRepository extends CrudRepository {
    constructor() {
        super(require('../models/catalogoModel'));
    }
}

module.exports = new CatalogoRepository();
