/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.IAbstractSyntaxTreeVisitor;
import org.eclipse.jdt.internal.compiler.lookup.*;


public class AnnotationReturnStatement extends ReturnStatement {
	public char[] description;

	public AnnotationReturnStatement(int s, int e, char[] descr) {
		super(null, s, e);
		this.description = descr;
		this.bits |= InsideAnnotation;
	}

	public void resolve(BlockScope scope) {
		MethodScope methodScope = scope.methodScope();
		MethodBinding methodBinding;
		TypeBinding methodType =
			(methodScope.referenceContext instanceof AbstractMethodDeclaration)
				? ((methodBinding = ((AbstractMethodDeclaration) methodScope.referenceContext).binding) == null 
					? null 
					: methodBinding.returnType)
				: VoidBinding;
		if (methodType == VoidBinding) {
			if (this.description != null) {
				scope.problemReporter().annotationUnexpectedTag(this.sourceStart, this.sourceEnd);
			}
		}
		else{
			if (this.description == null) {
				scope.problemReporter().annotationInvalidReturnTag(this.sourceStart, this.sourceEnd, true);
			}
		}
	}

	/* (non-Javadoc)
	 * Redefine to capture annotation specific signatures
	 * @see org.eclipse.jdt.internal.compiler.ast.AstNode#traverse(org.eclipse.jdt.internal.compiler.IAbstractSyntaxTreeVisitor, org.eclipse.jdt.internal.compiler.lookup.BlockScope)
	 */
	public void traverse(IAbstractSyntaxTreeVisitor visitor, BlockScope scope) {
		visitor.visit(this, scope);
		visitor.endVisit(this, scope);
	}
}
