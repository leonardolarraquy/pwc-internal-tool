package com.pwc.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to handle SPA (Single Page Application) routing.
 * 
 * Forwards all non-API, non-static file requests to index.html
 * so that React Router can handle client-side routing.
 * 
 * This prevents 404 errors when users refresh the page or navigate
 * directly to a React route like /dashboard or /configuration.
 * 
 * This controller is only active when index.html exists in static resources
 * (i.e., in production mode after frontend build is copied).
 * In development mode (frontend served by Vite), this controller is inactive.
 */
@Controller
@ConditionalOnResource(resources = "classpath:/static/index.html")
public class SpaController {
    
    /**
     * Forward SPA routes to index.html for React Router to handle.
     * 
     * Only handles single-segment paths without dots (like /dashboard, /login).
     * Static assets (/assets/**) and API routes (/api/**) are NOT matched.
     */
    @GetMapping(value = "/{path:[^\\.]*}")
    public String forwardSingle() {
        return "forward:/index.html";
    }
    
    /**
     * Forward nested SPA routes to index.html (like /assignments/gift).
     * 
     * Excludes paths starting with 'assets' or 'api' to avoid catching static files.
     */
    @GetMapping(value = "/{path:(?!assets|api)[^\\.]*}/**")
    public String forwardNested() {
        return "forward:/index.html";
    }
}
