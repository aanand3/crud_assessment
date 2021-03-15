package com.example.crud_assessment.donut;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.Map;

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

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id)
    {
        if (repo.existsById(id))
        {
            repo.deleteById(id);
            return "Donut " + id + " has been deleted";
        }
        else return "This Donut does not exist";
    }

    @PatchMapping("/{id}")
    public Donut update(@PathVariable Long id,
                        @RequestBody Map<String, Object> fields)
    {
        // it doesnt exist, so make and save it
        if (!repo.existsById(id))
        {
            ObjectMapper mapper = new ObjectMapper();
            Donut newDonut = mapper.convertValue(fields, Donut.class);
            return repo.save(newDonut);
        }

        // else we just update the current entry
        Donut currDonut = repo.findById(id).orElse(null);

        fields.forEach((key, value) ->
        {
            Field field = ReflectionUtils.findField(Donut.class, key);
            field.setAccessible(true);
            ReflectionUtils.setField(field, currDonut, value);
        });

        return repo.save(currDonut);
    }
}
