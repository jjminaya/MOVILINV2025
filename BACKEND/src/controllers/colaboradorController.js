const express = require('express');
const colaboradorService = require('../services/colaboradorService');
const router = express.Router();

router.get('/', async (req, res) => {
    try {
        const colaboradores = await colaboradorService.getAllColaboradores();
        res.json(colaboradores);
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener los Colaboradores', error: error.message });
    }
});

router.get('/:id', async (req, res) => {
    try {
        const colaborador = await colaboradorService.getColaboradorById(req.params.id);
        if (colaborador) {
            res.status(200).json(colaborador);
        } else {
            res.status(404).json({ message: 'Colaborador no encontrado' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener el Colaborador', error: error.message });
    }
});

router.get('/user/:username', async (req, res) => {
    try {
        const resultado = await colaboradorService.verificarUsuarioValido(req.params.username);

        if (resultado.valido) {
            res.status(200).json({
                valido: true,
                idUsuario: resultado.idUsuario
            });
        } else {
            res.status(404).json({
                valido: false,
                idUsuario: null
            });
        }
    } catch (error) {
        res.status(500).json({
            message: 'Hubo un error al verificar el usuario',
            error: error.message
        });
    }
});

router.post('/', async (req, res) => {
    try {
        const newColaborador = await colaboradorService.createColaborador(req.body);
        if (newColaborador) {
            res.status(201).json(newColaborador);
        } else {
            res.status(404).json({ message: 'No se pudo registrar el Colaborador' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al crear el Colaborador', error: error.message });
    }
});

router.put('/:id', async (req, res) => {
    try {
        const updateColaborador = await colaboradorService.updateColaborador(req.params.id, req.body);
        if (updateColaborador) {
            res.status(200).json(updateColaborador);
        } else {
            res.status(404).json({ message: 'Colaborador no actualizado' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al actualizar el Colaborador', error: error.message });
    }
});

router.delete('/:id', async (req, res) => {
    try {
        const deleteColaborador = await colaboradorService.deleteColaborador(req.params.id);
        if (deleteColaborador) {
            res.status(200).send();
        } else {
            res.status(400).json({ message: 'Colaborador no eliminado' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al eliminar el Colaborador', error: error.message });
    }
});

module.exports = router;