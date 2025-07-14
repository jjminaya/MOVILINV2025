const personaRepository = require('../repositories/personaRepository');

class PersonaService {
    getAllPersonas() {
        return personaRepository.findAll();
    }

    getPersonaById(id) {
        return personaRepository.findById(id);
    }

    createPersona(data) {
        return personaRepository.create(data);
    }

    updatePersona(id, data) {
        return personaRepository.update(id, data);
    }

    deletePersona(id) {
        return personaRepository.delete(id);
    }
}

module.exports = new PersonaService();
