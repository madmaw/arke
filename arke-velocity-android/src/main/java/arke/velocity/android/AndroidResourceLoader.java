package arke.velocity.android;

import android.content.res.Resources;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import java.io.InputStream;

public class AndroidResourceLoader extends ResourceLoader {

    private Resources resources;
    private String packageName;

    @Override
    public void commonInit(RuntimeServices rs, ExtendedProperties configuration) {
        super.commonInit(rs, configuration);

        this.resources = (Resources)rs.getProperty("android.content.res.Resources");
        this.packageName = (String)rs.getProperty("packageName");
    }

    @Override
    public void init(ExtendedProperties configuration) {
        // do nothing
    }

    @Override
    public InputStream getResourceStream(String source) throws ResourceNotFoundException {
        int id = resources.getIdentifier(source, "raw", this.packageName);
        return resources.openRawResource(id);
    }

    @Override
    public boolean resourceExists(String resourceName) {
        return resources.getIdentifier(resourceName, "raw", this.packageName) != 0;
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        return false;
    }

    @Override
    public long getLastModified(Resource resource) {
        return 0;
    }
}
