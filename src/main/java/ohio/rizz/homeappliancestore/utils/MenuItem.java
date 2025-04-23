package ohio.rizz.homeappliancestore.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MenuItem {
    private String name;
    private String url;
    private String icon;
    private boolean isActive;
}