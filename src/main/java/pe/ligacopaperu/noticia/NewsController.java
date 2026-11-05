package pe.ligacopaperu.noticia;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
public class NewsController {
    private final NewsService news;

    public NewsController(NewsService news) {
        this.news = news;
    }

    @GetMapping("/noticias")
    public String publicList(Model model) {
        model.addAttribute("articles", news.publicList());
        return "public/news";
    }

    @GetMapping("/noticias/{slug}")
    public String article(@PathVariable String slug, Model model) {
        NewsArticle article = news.publicArticle(slug);
        model.addAttribute("article", article);
        return "public/news-article";
    }

    @GetMapping("/admin/noticias")
    public String administrationList(Model model) {
        model.addAttribute("articles", news.administrationList());
        return "admin/news/list";
    }

    @GetMapping("/admin/noticias/nueva")
    public String createForm(Model model) {
        model.addAttribute("newsForm", new NewsForm());
        prepareForm(model, null, "/admin/noticias");
        return "admin/news/form";
    }

    @GetMapping("/admin/noticias/{id}/editar")
    public String editForm(@PathVariable long id, Model model) {
        model.addAttribute("newsForm", news.formFor(id));
        prepareForm(model, id, "/admin/noticias/" + id);
        return "admin/news/form";
    }

    @PostMapping({"/admin/noticias", "/admin/noticias/{id}"})
    public String save(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute NewsForm newsForm,
            BindingResult errors,
            Principal principal,
            Model model,
            RedirectAttributes redirect) {
        String action = id == null ? "/admin/noticias" : "/admin/noticias/" + id;
        if (errors.hasErrors()) {
            prepareForm(model, id, action);
            return "admin/news/form";
        }
        try {
            NewsArticle saved = news.save(id, newsForm, principal.getName());
            redirect.addFlashAttribute("success", "La noticia se guardó correctamente.");
            return "redirect:/admin/noticias";
        } catch (IllegalArgumentException exception) {
            errors.reject("news.invalid", exception.getMessage());
            prepareForm(model, id, action);
            return "admin/news/form";
        }
    }

    @PostMapping("/admin/noticias/{id}/archivar")
    public String archive(@PathVariable long id, Principal principal, RedirectAttributes redirect) {
        news.archive(id, principal.getName());
        redirect.addFlashAttribute("success", "La noticia se archivó.");
        return "redirect:/admin/noticias";
    }

    private void prepareForm(Model model, Long id, String action) {
        model.addAttribute("statuses", NewsStatus.values());
        model.addAttribute("action", action);
        model.addAttribute("title", id == null ? "Nueva noticia" : "Editar noticia");
    }
}
