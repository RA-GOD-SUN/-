package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.services.PersonService;
import com.example.springsecurityapplication.util.PersonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

// http:localhost:8080/auth/login

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final PersonValidator personValidator;

    private final PersonService personService;

    @Autowired
    public AuthController(PersonValidator personValidator, PersonService personService) {
        this.personValidator = personValidator;
        this.personService = personService;
    }

       @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/registration")
    public String registration(Model model){
        model.addAttribute("person", new Person());
        return "registration";
    }

    @PostMapping("/registration")
    public String resultRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){
        personValidator.validate(person, bindingResult);
        if(bindingResult.hasErrors()){
            return "registration";
        }
        personService.register(person);
        return "redirect:/index";
    }

    @GetMapping("/password/change")
    public String changePassword(Model model){
        model.addAttribute("person", new Person());
        return "password";
    }

    @PostMapping("/password/change")
    public String changePassword(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){

        personValidator.findUser(person, bindingResult);
        if(bindingResult.hasErrors()){
            return "password";
        }

        Person person_db = personService.getPersonFindByLogin(person);

        int id = person_db.getId();
        String password = person.getPassword();
        personService.changePassword(id, password);

        return "redirect:/index";
    }
    @GetMapping("/password/changepersonal")
    public String changePasswordPersonal(Model model){
        model.addAttribute("person", new Person());
        model.addAttribute("login", SecurityContextHolder.getContext().getAuthentication().getName());
        return "passwordPersonal";
    }

    @PostMapping("/password/changepersonal")
    public String changePasswordPersonal(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult, Model model){
        personValidator.findUser(person, bindingResult);
        if(bindingResult.hasErrors()){
            model.addAttribute("login", SecurityContextHolder.getContext().getAuthentication().getName());
            return "passwordPersonal";
        }

        Person person_db = personService.getPersonFindByLogin(person);
        int id = person_db.getId();
        String password = person.getPassword();
        personService.changePassword(id, password);

        return "redirect:/index";
    }



}

