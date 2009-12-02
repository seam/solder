package org.jboss.weld.test.extensions.util;

@SimpleAnnotation
@IntMemberAnnotation(someMember = 0, value = 1)
@MultipleMembers(booleanMember = true, byteMember = 1, charMember = 'c', doubleMember = 0, floatMember = 0, intMember = 1, intArrayMember = { 0, 1 }, longMember = 1, shortMember = 1)
public class AnnotatedClass
{

}
