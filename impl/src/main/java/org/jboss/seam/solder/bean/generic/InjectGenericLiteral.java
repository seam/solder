package org.jboss.seam.solder.bean.generic;

import javax.enterprise.util.AnnotationLiteral;

class InjectGenericLiteral extends AnnotationLiteral<InjectGeneric> implements InjectGeneric
{

   private static final long serialVersionUID = -1931707390692943775L;

   static final InjectGeneric INSTANCE = new InjectGenericLiteral();

}