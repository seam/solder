package org.jboss.seam.solder.bean.generic;

import javax.enterprise.util.AnnotationLiteral;

class GenericMarkerLiteral extends AnnotationLiteral<GenericMarker> implements GenericMarker
{

   private static final long serialVersionUID = -1931707390692943775L;

   static final GenericMarker INSTANCE = new GenericMarkerLiteral();

}