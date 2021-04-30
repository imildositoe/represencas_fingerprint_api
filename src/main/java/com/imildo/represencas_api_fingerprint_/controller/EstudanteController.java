package com.imildo.represencas_api_fingerprint_.controller;

import com.imildo.represencas_api_fingerprint_.model.Estudantes;
import com.imildo.represencas_api_fingerprint_.repository.EstudanteRepository;
import com.zkteco.biometric.FingerprintSensorErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author Imildo Sitoe
 *
 * */
@RestController
@RequestMapping(path = "/api")
public class EstudanteController {

    @Autowired
    private EstudanteRepository er;


    @GetMapping("/get_estudantes")
    @ResponseBody
    public Iterable<Estudantes> getEstudantes() {
        return er.findAll();
    }


    @GetMapping("/get_estudantes/{id_estudante}")
    @ResponseBody
    public Estudantes getEstudante(@PathVariable Integer id_estudante) {
        Optional<Estudantes> optional = er.findById(id_estudante);
        return optional.get();
    }


    @PostMapping("/save_estudante")
    @ResponseBody
    public Estudantes add(@RequestBody Estudantes estudantes) {
        er.save(estudantes);
        return estudantes;
    }


    @PostMapping("/add_url")
    @ResponseBody
    public String addUrl(@RequestParam Integer id_estudante, @RequestParam String nome, @RequestParam byte[] finger_template) {
        Estudantes estudantes = new Estudantes();
        estudantes.setId_estudante(id_estudante);
        estudantes.setNome(nome);
        estudantes.setFinger_template(finger_template);
        er.save(estudantes);
        return "Added Successfuly!";
    }


    @PutMapping("edit_estudante/{id_estudante}")
    @ResponseBody
    public ResponseEntity<Object> editEstudante(@RequestBody Estudantes estudantes, @PathVariable Integer id_estudante) {
        Optional<Estudantes> optional = er.findById(id_estudante);
        if (!optional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        estudantes.setId_estudante(id_estudante);
        er.save(estudantes);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("patch_estudante/{id_estudante}")
    @ResponseBody
    public String patchEstudante(@RequestBody Estudantes estudantes, @PathVariable Integer id_estudante) {
        Optional<Estudantes> optional = er.findById(id_estudante);
        Estudantes existing = optional.get();

        if (estudantes.getNome() != null) {
            existing.setNome(estudantes.getNome());
        }

        if (estudantes.getFinger_template() != null) {
            existing.setFinger_template(estudantes.getFinger_template());
        }
        er.save(existing);
        return "Patched Successfuly!";
    }


    @DeleteMapping("/delete_estudante/{id_estudante}")
    @ResponseBody
    public String deleteEstudante(@PathVariable Integer id_estudante) {
        er.deleteById(id_estudante);
        return "Deleted Successfuly!";
    }


    public String openDevice() {
        int ret = FingerprintSensorErrorCode.ZKFP_ERR_OK;
        return "Ret: " + ret;
    }

}
