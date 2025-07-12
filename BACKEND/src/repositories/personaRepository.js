const CrudRepository = require('../lib/crudRepository');

class PersonaRepository extends CrudRepository {
    constructor() {
        super(require('../models/personaModel'));
    }
}

module.exports = new PersonaRepository();
