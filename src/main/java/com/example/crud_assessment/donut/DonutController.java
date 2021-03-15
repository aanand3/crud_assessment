package com.example.crud_assessment.donut;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/donuts")
public class DonutController
{
    private final DonutRepository repo;

    public DonutController(DonutRepository repo)
    {
        this.repo = repo;
    }

    @GetMapping("")
    public Iterable<Donut> list() { return repo.findAll(); }

    @PostMapping("")
    public Donut create(@RequestBody Donut newDonut) { return repo.save(newDonut); }

    @GetMapping("/{id}")
    public Object read(@PathVariable Long id)
    {
        return repo.existsById(id) ?
                repo.findById(id) :
                "This Donut does not exist" ;
    }
}
