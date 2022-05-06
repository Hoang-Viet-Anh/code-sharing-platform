package platform.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import platform.database.Code;
import platform.database.CodeRepository;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class APIcontroller {
    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private HttpHeaders httpHeaders;

    @Autowired
    private Gson gson;

    @GetMapping("/api/code/latest")
    ResponseEntity<String> getLatestCodes() {
        List<Code> latest = codeRepository.findFirst10ByTimeLessThanEqualAndViewsLessThanEqualOrderByDateDesc(0, 0);
        latest = latest.stream().peek(a -> {
            a.setId(null);
            a.setTimerDate(null);
        }).collect(Collectors.toList());
        JsonArray array = new JsonArray();
        latest.forEach(a -> array.add(gson.toJsonTree(a)));
        return new ResponseEntity<>(gson.toJson(array), HttpStatus.OK);
    }

    @PostMapping("/api/code/new")
    ResponseEntity<String> setCode(@Valid @RequestBody Code code) {
        if (code.getTime() == null) {
            code.setTime(0L);
        }
        if (code.getViews() == null) {
            code.setViews(0L);
        }
        code = codeRepository.save(code);
        JsonObject object = new JsonObject();
        object.addProperty("id", code.getId().toString());
        return new ResponseEntity<>(gson.toJson(object), httpHeaders, HttpStatus.OK);
    }

    @GetMapping("/api/code/{id}")
    ResponseEntity<String> getCode(@PathVariable String id) {
        if (!codeRepository.existsById(UUID.fromString(id))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Code with specified id not found.");
        } else {
            Code code = codeRepository.findById(UUID.fromString(id));
            if (code.getTime() > 0) {
                Date date = new Date();
                code.setTime(code.getTime() - (date.getTime() / 1000 - code.getTimerDate().getTime() / 1000));
                code.setTimerDate(date);
                if (code.getTime() <= 0) {
                    codeRepository.delete(code);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A limit on the viewing time expired.");
                } else {
                    codeRepository.save(code);
                }
            } else {
                code.setTime(0L);
            }

            if (code.getViews() > 0) {
                code.setViews(code.getViews() - 1);
                if (code.getViews() == 0) {
                    codeRepository.delete(code);
                } else {
                    codeRepository.save(code);
                }
            } else {
                code.setViews(0L);
            }
            code.setId(null);
            code.setTimerDate(null);
            return new ResponseEntity<>(gson.toJson(code), HttpStatus.OK);
        }
    }
}
