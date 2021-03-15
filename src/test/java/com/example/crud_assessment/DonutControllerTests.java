package com.example.crud_assessment;

import com.example.crud_assessment.donut.Donut;
import com.example.crud_assessment.donut.DonutRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;

import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DonutControllerTests
{
    @Autowired
    MockMvc mvc;

    @Autowired
    DonutRepository repo;

    private Donut createDonut1()
    {
        Donut newDonut = new Donut();
        newDonut.setName("donut1");
        newDonut.setTopping("topping1");
        newDonut.setExpiration(new Date());

        return newDonut;
    }

    private Donut createDonut2()
    {
        Donut newDonut = new Donut();
        newDonut.setName("donut2");
        newDonut.setTopping("topping2");
        newDonut.setExpiration(new Date());

        return newDonut;
    }

    private Donut createDonut3()
    {
        Donut newDonut = new Donut();
        newDonut.setName("donut3");
        newDonut.setTopping("topping3");
        newDonut.setExpiration(new Date());

        return newDonut;
    }

    private void fillRepoWith3Donuts()
    {
        var list = List.of(createDonut1(), createDonut2(), createDonut3());

        repo.saveAll(list);
    }

    @Transactional
    @Rollback
    @Test
    public void listMappingReturnsAllItems() throws Exception
    {
        fillRepoWith3Donuts();

        MockHttpServletRequestBuilder getRequest = get("/donuts")
                                                    .contentType(MediaType.TEXT_PLAIN);

        mvc.perform(getRequest)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect( jsonPath("$[1].name", is("donut2")) );
    }

    @Transactional
    @Rollback
    @Test
    public void postMappingAddsDonut() throws Exception
    {
        Donut newDonut = createDonut1();

        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletRequestBuilder postRequest = post("/donuts")
                                                    .contentType(MediaType.APPLICATION_JSON)
                                                    .content(mapper.writeValueAsString(newDonut));

        mvc.perform(postRequest)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$.name", is(newDonut.getName())));
    }

    @Transactional
    @Rollback
    @Test
    public void getMappingReturnsSingleItem() throws Exception
    {
        fillRepoWith3Donuts();

        MockHttpServletRequestBuilder getRequest = get("/donuts/2")
                .contentType(MediaType.TEXT_PLAIN);

        mvc.perform(getRequest)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect( jsonPath("$.name", is("donut2")) );
    }

    @Transactional
    @Rollback
    @Test
    public void getMappingOnNonExistentItem() throws Exception
    {
        fillRepoWith3Donuts();

        MockHttpServletRequestBuilder getRequest = get("/donuts/2000")
                .contentType(MediaType.TEXT_PLAIN);

        mvc.perform(getRequest)
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect( content().string("This Donut does not exist") );
    }
}
