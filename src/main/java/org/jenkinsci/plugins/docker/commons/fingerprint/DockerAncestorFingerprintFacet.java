package org.jenkinsci.plugins.docker.commons.fingerprint;

import hudson.model.Fingerprint;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.Nonnull;

/**
 * Facet representing the fact that this Docker image was derived from another.
 * This facet would be added to the fingerprint corresponding to an image built using a {@code Dockerfile}.
 * {@link #getAncestorImageIds} indicates the ID of the image specified in the {@code FROM} instruction.
 * @see DockerFingerprints#addFromFacet
 * @see DockerAncestorFingerprintFacet
 */
public class DockerAncestorFingerprintFacet extends DockerRunPtrFingerprintFacet {

    private final Set<String> ancestorImageIds = new TreeSet<String>();

    DockerAncestorFingerprintFacet(Fingerprint fingerprint, long timestamp, String imageId) {
        super(fingerprint, timestamp, imageId);
    }

    synchronized void addAncestorImageId(@Nonnull String id) {
        ancestorImageIds.add(id);
    }

    /**
     * Gets the ancestor image that this image was built from.
     * In principle there could be several, in case distinct {@code Dockerfile}s used distinct {@code FROM} images,
     * yet wound up producing the same result (because some corresponded to intermediate layers which were cached).
     * This is unlikely but possible.
     * The set may be empty in case you built a base image ({@code FROM scratch}), in which case there is no ID for the ancestor.
     * @return a set of 64-digit IDs, typically a singleton
     */
    public synchronized @Nonnull Set<String> getAncestorImageIds() {
        return new TreeSet<String>(ancestorImageIds);
    }

}