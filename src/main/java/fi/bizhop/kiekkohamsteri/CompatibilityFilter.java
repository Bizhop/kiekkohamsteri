package fi.bizhop.kiekkohamsteri;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CompatibilityFilter implements Filter {
    static class FilteredRequest extends HttpServletRequestWrapper {
        private static final Map<String, String> CONVERSIONS = new HashMap<>();

        static {
            //mold
            CONVERSIONS.put("mold.valmistaja.valmistaja", "mold.manufacturer.name");
            CONVERSIONS.put("mold.nopeus", "mold.speed");
            CONVERSIONS.put("mold.liito", "mold.glide");
            CONVERSIONS.put("mold.vakaus", "mold.stability");
            CONVERSIONS.put("mold.feidi", "mold.fade");
            CONVERSIONS.put("mold.kiekko", "mold.name");
            CONVERSIONS.put("kiekko", "name");

            //disc
            CONVERSIONS.put("paino", "weight");
            CONVERSIONS.put("member.username", "owner.username");
            CONVERSIONS.put("hinta", "price");
            CONVERSIONS.put("hohto", "glow");
            CONVERSIONS.put("spessu", "special");

            //plastic
            CONVERSIONS.put("muovi.muovi", "plastic.name");
            CONVERSIONS.put("muovi", "name");
        }

        public FilteredRequest(ServletRequest request) {
            super((HttpServletRequest) request);
        }

        @Override
        public String getParameter(String paramName) {
            var value = super.getParameter(paramName);
            if(value == null) return null;
            if(!"sort".equals(paramName)) return value;

            return sanitize(value);
        }

        @Override
        public String[] getParameterValues(String paramName) {
            String[] values = super.getParameterValues(paramName);
            if(values == null) return null;
            if(!"sort".equals(paramName)) return values;

            for(int i=0; i < values.length; i++) {
                values[i] = sanitize(values[i]);
            }
            return values;
        }

        private String sanitize(String value) {
            var field = value.split(",")[0];
            var replacement = CONVERSIONS.getOrDefault(field, field);
            return value.replace(field, replacement);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new FilteredRequest(servletRequest), servletResponse);
    }
}
