package org.c3lang.intellij.psi.impl

import ai.grazie.utils.dropPostfix
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiReference
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference
import org.c3lang.intellij.psi.C3CallExpr
import org.c3lang.intellij.psi.C3PathIdentExpr
import org.c3lang.intellij.psi.C3PsiElement
import org.c3lang.intellij.psi.FullyQualifiedName
import org.c3lang.intellij.psi.reference.C3ReferenceBase

abstract class C3CallExprMixinImpl(node: ASTNode) : C3PsiElementImpl(node), C3CallExpr {

    override val fqName: FullyQualifiedName
        get() = FullyQualifiedName(moduleDefinition.moduleName, lastChild.text)

    override fun getReference(): PsiReference? {
        return PsiMultiReference(
            arrayOf(ImportPathReference(this)),
            this
        )
    }

    private class ImportPathReference(element: C3CallExpr) : C3ReferenceBase<C3CallExpr>(element) {
        private val pathExpr = element.expr as? C3PathIdentExpr

        override fun multiResolve(): Collection<C3PsiElement> {
            pathExpr ?: return emptyList()

            return element.moduleDefinition.getImportOf(pathExpr)
        }

        override fun getRangeInElement(): TextRange {
            val path = pathExpr?.pathIdent?.path ?: return TextRange.EMPTY_RANGE
            return TextRange.create(0, path.text.dropPostfix("::").length)
        }
    }

//    override fun getNameIdentifier(): PsiElement? {
//        return node.findChildByType(C3Types.PATH_IDENT_EXPR)
//            ?.findChildByType(C3Types.PATH_IDENT)
//            ?.getChildren(TokenSet.create(C3Types.IDENT))
//            ?.lastOrNull()?.psi
//    }
//
//    override fun getName(): String? {
//        return nameIdentifier?.text
//    }
//
//    override fun setName(name: String): PsiElement {
//        return this
//    }

//    override fun getReference(): PsiReference = PsiMultiReference(
//        arrayOf(
//            ImportPathReference(this)
//        ),
//        this
//    )

}

