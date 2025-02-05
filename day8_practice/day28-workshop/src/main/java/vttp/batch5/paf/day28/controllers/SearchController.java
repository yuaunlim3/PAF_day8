package vttp.batch5.paf.day28.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import vttp.batch5.paf.day28.models.Game;
import vttp.batch5.paf.day28.services.GameService;

@Controller
@RequestMapping({"/","index.html"})
public class SearchController {
    @Autowired private GameService gameService;

    @GetMapping("/search")
    public ModelAndView start(@RequestParam("q") String gameName){
        ModelAndView mav = new ModelAndView();
        mav.setViewName("search_result");
        List<Game> games = gameService.getGames(gameName);
        mav.addObject("games",games);

        return mav;
    }
}
