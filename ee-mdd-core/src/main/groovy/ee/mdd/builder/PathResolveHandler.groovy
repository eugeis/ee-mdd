package ee.mdd.builder

import ee.mdd.model.Base

/**
 * @author Eugen Eisler
 */
class PathResolveHandler extends AbstractResolveHandler {
    public final static PREFIX_ROOT_PREFIX = '//'
    public final static PREFIX_UP_STRUCTURE_UNIT = '/'
    public final static SEPARATOR = '.'

    Map<String, Object> resolved = new HashMap<>()
    Map<String, List<Closure>> notResolvedPathRefToResolvers = [:]

    Closure setter

    Closure rootFindParentMatcher
    Closure midPartFindMatcher
    Closure lastPartFindMatcher

    @Override
    void on(String ref, Base el, Base parent) {
        notResolvedPathRefToResolvers.remove()?.each { it() }
    }

    @Override
    void addResolveRequest(String ref, Base el, Base parent) {
        resolveParts(el, parent, buildParts(ref))
    }

    @Override
    Base resolve(String ref, Base el, Base parent) {
        resolveParts(el, parent, buildParts(ref), 0, false)
    }

    boolean isPath(String ref, Base el, Base parent) {
        ref.contains(SEPARATOR) || ref.startsWith(PREFIX_ROOT_PREFIX) || ref.startsWith(PREFIX_UP_STRUCTURE_UNIT)
    }

    private
    def resolveParts(Base item, Base parent, String[] parts, int partIndex = 0, boolean trackNotResolved = true) {
        def el = item
        def elParent = parent

        for (int i = partIndex; i < parts.size(); i++) {
            String part = parts[i]

            def resolved
            if (partIndex == 0) {
                resolved = rootFindParentMatcher(part, el, parent)
            } else if (i == parts.size() - 1) {
                resolved = midPartFindMatcher(part, el, parent)
            } else {
                resolved = lastPartFindMatcher(part, el, parent)
            }

            if (resolved) {
                el = resolved
                elParent = parent
            } else if (trackNotResolved) {
                if (!notResolvedPathRefToResolvers.containsKey(part)) {
                    notResolvedPathRefToResolvers[part] = []
                }
                notResolvedPathRefToResolvers[part] << { resolveParts(el, elParent, parts, i) }
                //println "Can not resolve $part of $parts for $el'"
                el = null
                break
            }
        }

        if (trackNotResolved && el) {
            setter(item, el)
        }
        el
    }

    protected String[] buildParts(String ref) {
        ref.split("\\$SEPARATOR")
    }

}
