package platform.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;
import platform.database.Code;
import platform.database.CodeRepository;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class WebController {
    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    Gson gson;

    @GetMapping("/code/{id}")
    String getCode(@PathVariable String id, Model model, HttpServletResponse response) {
        Code code;
        if (!codeRepository.existsById(UUID.fromString(id))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Code with specified id not found.");
        } else {
            code = codeRepository.findById(UUID.fromString(id));
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
                    code.setViews(null);
                } else {
                    codeRepository.save(code);
                }
            } else {
                code.setViews(0L);
            }
            code.setId(null);
            code.setTimerDate(null);
        }
        JsonObject object = gson.toJsonTree(code).getAsJsonObject();
        String date = object.getAsJsonPrimitive("date").getAsString();
        response.setContentType("text/html");
        model.addAttribute("date", date);
        model.addAttribute("code", code);
        return "code";
    }

    @GetMapping("/code/new")
    String setCode(Model model, HttpServletResponse response) {
        response.setContentType("text/html");
        return "setCode";
    }

    @GetMapping("/code/latest")
    String getLatestCodes(Model model) {
        List<Code> latest = codeRepository.findFirst10ByTimeLessThanEqualAndViewsLessThanEqualOrderByDateDesc(0, 0);
        latest = latest.stream().peek(a -> a.setFormattedDate(a.formatDate())).collect(Collectors.toList());
        model.addAttribute("codes", latest);
        return "latestCodes";
    }
}
