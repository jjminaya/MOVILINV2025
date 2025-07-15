const express = require('express');
const personaService = require('../services/personaService');
const router = express.Router();

router.get('/', async (req, res) => {
    try {
        const personas = await personaService.getAllPersonas();
        res.json(personas);
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener las personas', error: error.message });
    }
});

router.get('/:id', async (req, res) => {
    try {
        const persona = await personaService.getPersonaById(req.params.id);
        if (persona) {
            res.json(persona);
        } else {
            res.status(404).json({ message: 'Persona no encontrada' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener la persona', error: error.message });
    }
});

router.post('/', async (req, res) => {
    try {
        const newPersona = await personaService.createPersona(req.body);
        res.status(201).json(newPersona);
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al crear la persona', error: error.message });
    }
});

router.put('/:id', async (req, res) => {
    try {
        const updatedPersona = await personaService.updatePersona(req.params.id, req.body);
        res.json(updatedPersona);
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al actualizar la persona', error: error.message });
    }
});

router.delete('/:id', async (req, res) => {
    try {
        const deleted = await personaService.deletePersona(req.params.id);
        if (deleted) {
            res.json({ message: 'Persona eliminada correctamente' });
        } else {
            res.status(404).json({ message: 'Persona no encontrada' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al eliminar la persona', error: error.message });
    }
});

module.exports = router;
