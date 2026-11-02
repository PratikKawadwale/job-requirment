package com.recruitment.web;

import com.recruitment.model.Candidate;
import com.recruitment.model.EligibilityResult;
import com.recruitment.service.ApplicationService;
import com.recruitment.service.CandidateService;
import com.recruitment.service.CompanyService;
import com.recruitment.service.InterviewService;
import com.recruitment.service.JobService;
import com.recruitment.service.OfferService;
import com.recruitment.service.PlacementService;
import com.recruitment.service.ReportService;
import com.recruitment.service.UserService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Built-in Lightweight HTTP Server serving both the static Web UI
 * and REST JSON API endpoints powered by pure JDBC and rpms_db.
 */
public class WebServer {

    private final int port;
    private final File webRoot;
    private HttpServer server;

    private final CandidateService candidateService = new CandidateService();
    private final CompanyService companyService = new CompanyService();
    private final JobService jobService = new JobService();
    private final ApplicationService applicationService = new ApplicationService();
    private final InterviewService interviewService = new InterviewService();
    private final OfferService offerService = new OfferService();
    private final PlacementService placementService = new PlacementService();
    private final ReportService reportService = new ReportService();
    private final UserService userService = new UserService();

    public WebServer(int port, File webRoot) {
        this.port = port;
        this.webRoot = webRoot;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));

        // API Handler
        server.createContext("/api", new ApiHandler());

        // Static Assets Handler
        server.createContext("/", new StaticFileHandler());

        server.start();
        System.out.println("==================================================================");
        System.out.println(" RPMS Web Server running at: http://localhost:" + port + "/");
        System.out.println(" Web UI served from        : " + webRoot.getAbsolutePath());
        System.out.println(" Live MySQL Database       : rpms_db");
        System.out.println("==================================================================");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    private class ApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Enable CORS
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> queryParams = parseQueryParams(query);

            try {
                if (path.equals("/api/health")) {
                    sendJson(exchange, 200, "{\"status\":\"UP\",\"database\":\"rpms_db\"}");
                } else if (path.equals("/api/auth/login") || path.equals("/api/login")) {
                    handleLogin(exchange);
                } else if (path.equals("/api/dashboard/stats")) {
                    handleDashboardStats(exchange);
                } else if (path.equals("/api/candidates")) {
                    handleCandidates(exchange, queryParams);
                } else if (path.equals("/api/companies")) {
                    handleCompanies(exchange, queryParams);
                } else if (path.equals("/api/jobs") || path.equals("/api/jobs/open")) {
                    handleJobs(exchange, path, queryParams);
                } else if (path.equals("/api/applications")) {
                    handleApplications(exchange, queryParams);
                } else if (path.equals("/api/eligibility/check")) {
                    handleEligibilityCheck(exchange);
                } else if (path.equals("/api/interviews")) {
                    handleInterviews(exchange);
                } else if (path.equals("/api/offers")) {
                    handleOffers(exchange);
                } else if (path.equals("/api/placements")) {
                    handlePlacements(exchange);
                } else if (path.startsWith("/api/reports/")) {
                    handleReports(exchange, path);
                } else {
                    sendJson(exchange, 404, "{\"error\":\"Endpoint not found: " + JsonUtil.escape(path) + "\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendJson(exchange, 500, "{\"error\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
            }
        }

        private void handleLogin(HttpExchange exchange) throws Exception {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJson(exchange, 405, "{\"error\":\"Method Not Allowed\"}");
                return;
            }
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> params = parseFormBody(body);
            String username = params.getOrDefault("username", params.getOrDefault("email", "")).trim();
            String password = params.getOrDefault("password", "").trim();

            var opt = userService.authenticate(username, password);
            if (opt.isPresent()) {
                var u = opt.get();
                String role = u.getRoleName() != null ? u.getRoleName() : "Recruiter";
                String json = String.format("{\"success\":true,\"user\":{\"userId\":%d,\"username\":\"%s\",\"fullName\":\"%s\",\"email\":\"%s\",\"role\":\"%s\"}}",
                        u.getUserId(),
                        JsonUtil.escape(u.getUsername()),
                        JsonUtil.escape(u.getFullName()),
                        JsonUtil.escape(u.getEmail()),
                        JsonUtil.escape(role));
                sendJson(exchange, 200, json);
            } else {
                sendJson(exchange, 401, "{\"success\":false,\"error\":\"Invalid credentials. Try recruiter@demo.com / demo123 or admin / admin123\"}");
            }
        }

        private void handleDashboardStats(HttpExchange exchange) throws Exception {
            int totalCandidates = candidateService.getAllCandidates().size();
            long activeCandidates = candidateService.getAllCandidates().stream()
                    .filter(c -> "ACTIVE".equalsIgnoreCase(c.getProfileStatus()))
                    .count();
            int totalCompanies = companyService.getAllCompanies().size();
            int openJobs = jobService.getOpenJobs().size();
            int totalApplications = applicationService.getAllApplications().size();
            int scheduledInterviews = interviewService.getUpcomingInterviews().size();
            int totalOffers = offerService.getAllOffers().size();
            int confirmedPlaced = placementService.getPlacedCandidates().size();

            String json = String.format("{" +
                            "\"totalCandidates\":%d," +
                            "\"activeCandidates\":%d," +
                            "\"totalCompanies\":%d," +
                            "\"openJobs\":%d," +
                            "\"totalApplications\":%d," +
                            "\"interviewsScheduled\":%d," +
                            "\"totalOffers\":%d," +
                            "\"totalPlaced\":%d" +
                            "}",
                    totalCandidates, activeCandidates, totalCompanies, openJobs,
                    totalApplications, scheduledInterviews, totalOffers, confirmedPlaced);
            sendJson(exchange, 200, json);
        }

        private void handleCandidates(HttpExchange exchange, Map<String, String> queryParams) throws Exception {
            String keyword = queryParams.get("search");
            List<Candidate> list = candidateService.getAllCandidates();

            if (keyword != null && !keyword.isBlank()) {
                String k = keyword.trim().toLowerCase();
                list = list.stream().filter(c ->
                    (c.getFullName() != null && c.getFullName().toLowerCase().contains(k)) ||
                    (c.getCandidateCode() != null && c.getCandidateCode().toLowerCase().contains(k)) ||
                    (c.getEmail() != null && c.getEmail().toLowerCase().contains(k)) ||
                    (c.getMobile() != null && c.getMobile().contains(k)) ||
                    (c.getPrimaryTechnology() != null && c.getPrimaryTechnology().toLowerCase().contains(k)) ||
                    (c.getQualification() != null && c.getQualification().toLowerCase().contains(k)) ||
                    (c.getCurrentLocation() != null && c.getCurrentLocation().toLowerCase().contains(k)) ||
                    (c.getCollege() != null && c.getCollege().toLowerCase().contains(k))
                ).toList();
            }

            String tech = queryParams.get("tech");
            if (tech != null && !tech.isBlank()) {
                list = list.stream().filter(c -> c.getPrimaryTechnology() != null && c.getPrimaryTechnology().toLowerCase().contains(tech.toLowerCase())).toList();
            }
            String year = queryParams.get("year");
            if (year != null && !year.isBlank()) {
                try {
                    int y = Integer.parseInt(year);
                    list = list.stream().filter(c -> c.getPassingYear() == y).toList();
                } catch (NumberFormatException ignored) {}
            }
            String qual = queryParams.get("qual");
            if (qual != null && !qual.isBlank()) {
                list = list.stream().filter(c -> c.getQualification() != null && c.getQualification().toLowerCase().contains(qual.toLowerCase())).toList();
            }
            String status = queryParams.get("status");
            if (status != null && !status.isBlank()) {
                list = list.stream().filter(c -> status.equalsIgnoreCase(c.getProfileStatus()) || status.equalsIgnoreCase(c.getPlacementStatus())).toList();
            }

            sendJson(exchange, 200, JsonUtil.listToJson(list, JsonUtil::toJson));
        }

        private void handleCompanies(HttpExchange exchange, Map<String, String> queryParams) throws Exception {
            String keyword = queryParams.get("search");
            var list = companyService.getAllCompanies();
            if (keyword != null && !keyword.isBlank()) {
                String k = keyword.trim().toLowerCase();
                list = list.stream().filter(c ->
                    (c.getCompanyName() != null && c.getCompanyName().toLowerCase().contains(k)) ||
                    (c.getCompanyCode() != null && c.getCompanyCode().toLowerCase().contains(k)) ||
                    (c.getIndustry() != null && c.getIndustry().toLowerCase().contains(k)) ||
                    (c.getLocation() != null && c.getLocation().toLowerCase().contains(k)) ||
                    (c.getHrName() != null && c.getHrName().toLowerCase().contains(k))
                ).toList();
            }
            sendJson(exchange, 200, JsonUtil.listToJson(list, JsonUtil::toJson));
        }

        private void handleJobs(HttpExchange exchange, String path, Map<String, String> queryParams) throws Exception {
            String keyword = queryParams.get("search");
            var list = path.contains("open") ? jobService.getOpenJobs() : jobService.getAllJobs();
            if (keyword != null && !keyword.isBlank()) {
                String k = keyword.trim().toLowerCase();
                list = list.stream().filter(j ->
                    (j.getJobTitle() != null && j.getJobTitle().toLowerCase().contains(k)) ||
                    (j.getJobCode() != null && j.getJobCode().toLowerCase().contains(k)) ||
                    (j.getCompanyName() != null && j.getCompanyName().toLowerCase().contains(k)) ||
                    (j.getLocation() != null && j.getLocation().toLowerCase().contains(k)) ||
                    (j.getTechnology() != null && j.getTechnology().toLowerCase().contains(k))
                ).toList();
            }
            sendJson(exchange, 200, JsonUtil.listToJson(list, JsonUtil::toJson));
        }

        private void handleApplications(HttpExchange exchange, Map<String, String> queryParams) throws Exception {
            String keyword = queryParams.get("search");
            var list = applicationService.getAllApplications();
            if (keyword != null && !keyword.isBlank()) {
                String k = keyword.trim().toLowerCase();
                list = list.stream().filter(a ->
                    (a.getCandidateName() != null && a.getCandidateName().toLowerCase().contains(k)) ||
                    (a.getJobTitle() != null && a.getJobTitle().toLowerCase().contains(k)) ||
                    (a.getCompanyName() != null && a.getCompanyName().toLowerCase().contains(k)) ||
                    (a.getApplicationCode() != null && a.getApplicationCode().toLowerCase().contains(k)) ||
                    (a.getApplicationStatus() != null && a.getApplicationStatus().toLowerCase().contains(k))
                ).toList();
            }
            sendJson(exchange, 200, JsonUtil.listToJson(list, JsonUtil::toJson));
        }

        private void handleEligibilityCheck(HttpExchange exchange) throws Exception {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> params = parseFormBody(body);
            int candidateId = Integer.parseInt(params.getOrDefault("candidateId", "0"));
            int jobId = Integer.parseInt(params.getOrDefault("jobId", "0"));

            EligibilityResult result = applicationService.checkEligibility(candidateId, jobId);
            sendJson(exchange, 200, JsonUtil.toJson(result));
        }

        private void handleInterviews(HttpExchange exchange) throws Exception {
            var list = interviewService.getUpcomingInterviews();
            sendJson(exchange, 200, JsonUtil.listToJson(list, JsonUtil::toJson));
        }

        private void handleOffers(HttpExchange exchange) throws Exception {
            var list = offerService.getAllOffers();
            sendJson(exchange, 200, JsonUtil.listToJson(list, JsonUtil::toJson));
        }

        private void handlePlacements(HttpExchange exchange) throws Exception {
            var list = placementService.getAllPlacements();
            sendJson(exchange, 200, JsonUtil.listToJson(list, JsonUtil::toJson));
        }

        private void handleReports(HttpExchange exchange, String path) throws Exception {
            if (path.endsWith("candidates-by-tech")) {
                var map = reportService.getCandidatesByTechnology();
                sendJson(exchange, 200, JsonUtil.mapToJson(map));
            } else if (path.endsWith("open-jobs")) {
                var list = reportService.getOpenJobs();
                sendJson(exchange, 200, JsonUtil.listToJson(list, JsonUtil::toJson));
            } else if (path.endsWith("applications-by-status")) {
                var map = reportService.getApplicationsByStatus();
                sendJson(exchange, 200, JsonUtil.mapToJson(map));
            } else if (path.endsWith("company-placements")) {
                var map = reportService.getCompanyWisePlacements();
                sendJson(exchange, 200, JsonUtil.mapToJson(map));
            } else if (path.endsWith("placed-candidates")) {
                var list = reportService.getPlacedCandidates();
                sendJson(exchange, 200, JsonUtil.listToJson(list, JsonUtil::toJson));
            } else if (path.endsWith("unplaced-candidates")) {
                var list = reportService.getUnplacedCandidates();
                sendJson(exchange, 200, JsonUtil.listToJson(list, JsonUtil::toJson));
            } else {
                sendJson(exchange, 404, "{\"error\":\"Unknown report\"}");
            }
        }
    }

    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            if (requestPath.equals("/") || requestPath.equals("/index.html")) {
                requestPath = "/login.html";
            }

            File file = new File(webRoot, requestPath);
            if (!file.exists() || file.isDirectory()) {
                String notFound = "<h1>404 File Not Found</h1><p>" + requestPath + "</p>";
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            String contentType = getMimeType(file.getName());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.sendResponseHeaders(200, file.length());

            try (InputStream is = new FileInputStream(file);
                 OutputStream os = exchange.getResponseBody()) {
                is.transferTo(os);
            }
        }
    }

    private static String getMimeType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".html")) return "text/html; charset=UTF-8";
        if (lower.endsWith(".css")) return "text/css; charset=UTF-8";
        if (lower.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".json")) return "application/json; charset=UTF-8";
        return "application/octet-stream";
    }

    private static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isBlank()) return map;
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String val = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            map.put(key, val);
        }
        return map;
    }

    private static Map<String, String> parseFormBody(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isBlank()) return map;
        if (body.startsWith("{") && body.endsWith("}")) {
            // Simple JSON key-value extraction
            String trimmed = body.substring(1, body.length() - 1);
            for (String pair : trimmed.split(",")) {
                String[] parts = pair.split(":", 2);
                if (parts.length == 2) {
                    String k = parts[0].replaceAll("[\"\\s]", "");
                    String v = parts[1].replaceAll("[\"\\s]", "");
                    map.put(k, v);
                }
            }
        } else {
            return parseQueryParams(body);
        }
        return map;
    }
}
