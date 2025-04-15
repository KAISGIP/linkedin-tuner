import React, { useState } from 'react';
import './PostEditor.css';

interface Post {
    id?: string;
    content: string;
    correctedContent?: string;
    status?: string;
    linkedInPostId?: string;
}

const PostEditor: React.FC = () => {
    const [post, setPost] = useState<Post>({ content: '' });
    const [isLoading, setIsLoading] = useState(false);
    const [savedPosts, setSavedPosts] = useState<Post[]>([]);
    const [error, setError] = useState<string | null>(null);

    const handleCorrection = async () => {
        setIsLoading(true);
        setError(null);

        try {
            const response = await fetch('http://localhost:8080/api/posts/correct', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    messages: [{
                        role: "user",
                        content: post.content
                    }]
                }),
                credentials: 'include',
            });

            if (!response.ok) {
                if (response.status === 401) {
                    setError('Session expirée. Veuillez vous reconnecter.');
                    return;
                }
                throw new Error(`Erreur HTTP: ${response.status}`);
            }

            const data = await response.json();
            setPost(prev => ({ ...prev, correctedContent: data.correctedText }));
        } catch (error) {
            console.error('Erreur lors de la correction:', error);
            setError('Une erreur est survenue lors de la correction du texte.');
        } finally {
            setIsLoading(false);
        }
    };

    const handleSave = async () => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await fetch('http://localhost:8080/api/posts/save', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    content: post.content,
                    correctedContent: post.correctedContent,
                    status: 'DRAFT'
                }),
                credentials: 'include',
            });

            if (!response.ok) {
                throw new Error(`Erreur HTTP: ${response.status}`);
            }

            const data = await response.json();
            setSavedPosts(prev => [...prev, data]);
            setPost({ content: '', correctedContent: '' });
        } catch (error) {
            console.error('Erreur lors de la sauvegarde:', error);
            setError('Une erreur est survenue lors de la sauvegarde du post.');
        } finally {
            setIsLoading(false);
        }
    };

    const handlePublish = async (postToPublish: Post) => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await fetch('http://localhost:8080/api/posts/publish', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    id: postToPublish.id,
                    correctedContent: postToPublish.correctedContent
                }),
                credentials: 'include',
            });

            if (!response.ok) {
                const errorData = await response.text();
                throw new Error(errorData || `Erreur HTTP: ${response.status}`);
            }

            const data = await response.json();
            alert('Post publié avec succès! ID: ' + data);
            
            // Mettre à jour le statut du post dans la liste des posts sauvegardés
            setSavedPosts(prev => prev.map(post => 
                post.id === postToPublish.id 
                    ? { ...post, status: 'PUBLISHED', linkedInPostId: data }
                    : post
            ));
        } catch (error) {
            console.error('Erreur lors de la publication:', error);
            setError('Une erreur est survenue lors de la publication sur LinkedIn. Veuillez vérifier votre connexion et réessayer.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="post-editor">
            <div className="editor-section">
                <h2>Éditeur de Post LinkedIn</h2>
                {error && <div className="error-message">{error}</div>}
                <div className="editor-container">
                    <div className="input-section">
                        <h3>Votre texte</h3>
                        <textarea
                            value={post.content}
                            onChange={(e) => setPost(prev => ({ ...prev, content: e.target.value }))}
                            placeholder="Écrivez votre post ici..."
                            rows={10}
                        />
                        <button 
                            onClick={handleCorrection} 
                            disabled={isLoading || !post.content.trim()}
                        >
                            {isLoading ? 'Correction en cours...' : 'Corriger le texte'}
                        </button>
                    </div>
                    <div className="output-section">
                        <h3>Texte corrigé</h3>
                        <div className="corrected-text">
                            {post.correctedContent || 'Le texte corrigé apparaîtra ici...'}
                        </div>
                    </div>
                </div>
            </div>

            <div className="button-group">
                <button onClick={handleSave} disabled={isLoading || !post.correctedContent}>
                    {isLoading ? 'Sauvegarde en cours...' : 'Sauvegarder'}
                </button>
            </div>

            <div className="saved-posts">
                <h2>Posts Sauvegardés</h2>
                {savedPosts.map((savedPost, index) => (
                    <div key={index} className="saved-post">
                        <h3>Post #{index + 1}</h3>
                        <div className="post-content">
                            <p><strong>Original:</strong> {savedPost.content}</p>
                            <p><strong>Corrigé:</strong> {savedPost.correctedContent}</p>
                        </div>
                        <button 
                            onClick={() => handlePublish(savedPost)}
                            disabled={isLoading}
                        >
                            Publier sur LinkedIn
                        </button>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default PostEditor; 